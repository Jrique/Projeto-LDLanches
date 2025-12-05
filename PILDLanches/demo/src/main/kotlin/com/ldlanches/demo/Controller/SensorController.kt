package com.ldlanches.demo.Controller


import com.ldlanches.demo.Config.SensorStorage
import com.ldlanches.demo.DTO.SensorDTO

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController



@RestController
@RequestMapping("/sensors")
class SensorController {

    @GetMapping("/freezer")
    fun getTemperature(): ResponseEntity<SensorDTO> {
        val data = SensorDTO(
            temp = SensorStorage.temp,
            time = SensorStorage.lastUpdate
        )
        return ResponseEntity.ok(data)
    }
}