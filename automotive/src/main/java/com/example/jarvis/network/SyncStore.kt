package com.example.jarvis.network

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONArray
import org.json.JSONObject

/**
 * Store simple con Flows para sincronizar lo que llega por WebSocket.
 * Acepta formas comunes:
 * - { "jobs": [ ... ] }
 * - { "tools": [ ... ] }
 * - { "jobId"|"job_id": "X", "tools": [ ... ] }
 * - { "payload": { jobs: [...], tools: [...] } }
 *
 * Los modelos del móvil son:
 *   Job: id, clientName, worksite, status, ...
 *   Tool: id, name, model, battery, temperature, availability, url, ...
 * Aquí derivamos campos que necesita la UI del carro.
 */
object SyncStore {

    data class CarJob(val id: String, val title: String, val status: String)
    data class CarTool(
        val id: String,
        val title: String,
        val subtitle: String,
        val battery: Int,
        val temperature: Int,
        val status: String,
        val url: String
    )

    val jobs = MutableStateFlow<List<CarJob>>(emptyList())
    val tools = MutableStateFlow<List<CarTool>>(emptyList())
    val jobTools = MutableStateFlow<Map<String, List<CarTool>>>(emptyMap())

    fun applyIncomingJson(msg: JSONObject) {

        msg.optJSONArray("jobs")?.let { jobs.value = parseJobs(it) }
        msg.optJSONArray("tools")?.let { tools.value = parseTools(it) }

        val jobIdRoot = msg.optString("jobId", msg.optString("job_id", ""))
        if (jobIdRoot.isNotBlank() && msg.has("tools")) {
            val t = parseTools(msg.getJSONArray("tools"))
            jobTools.update { it.toMutableMap().apply { put(jobIdRoot, t) } }
        }


        msg.optJSONObject("payload")?.let { payload ->
            payload.optJSONArray("jobs")?.let { jobs.value = parseJobs(it) }
            payload.optJSONArray("tools")?.let { tools.value = parseTools(it) }
            val jid = payload.optString("jobId", payload.optString("job_id", ""))
            if (jid.isNotBlank() && payload.has("tools")) {
                val t = parseTools(payload.getJSONArray("tools"))
                jobTools.update { it.toMutableMap().apply { put(jid, t) } }
            }
        }
    }

    private fun parseJobs(arr: JSONArray): List<CarJob> {
        val out = mutableListOf<CarJob>()
        for (i in 0 until arr.length()) {
            val o = arr.optJSONObject(i) ?: continue
            val id = o.optString("id", o.optString("_id", o.optString("uuid", "job_$i")))
            val status = o.optString("status", "pending")
            val title = when {
                o.has("title") -> o.optString("title")
                o.has("clientName") && o.has("worksite") ->
                    "${o.optString("clientName")} - ${o.optString("worksite")}"
                o.has("clientName") -> o.optString("clientName")
                else -> "Trabajo $id"
            }
            out += CarJob(id = id, title = title, status = status)
        }
        return out
    }

    private fun parseTools(arr: JSONArray): List<CarTool> {
        val out = mutableListOf<CarTool>()
        for (i in 0 until arr.length()) {
            val o = arr.optJSONObject(i) ?: continue
            val availability = o.optString("availability", "available")
            val status = when (availability) {
                "in_use" -> "WARNING"
                else -> "OK"
            }
            out += CarTool(
                id = o.optString("id", o.optString("_id", "tool_$i")),
                title = o.optString("name", o.optString("title", "Tool ${i+1}")),
                subtitle = o.optString("model", o.optString("subtitle", "")),
                battery = o.optInt("battery", 0),
                temperature = o.optInt("temperature", 0),
                status = status,
                url = o.optString("url", "")
            )
        }
        return out
    }
}