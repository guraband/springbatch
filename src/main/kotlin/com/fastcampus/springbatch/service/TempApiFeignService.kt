package com.fastcampus.springbatch.service

import com.fastcampus.springbatch.dto.TempApiRequest
import com.fastcampus.springbatch.dto.TempApiResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@Service
@FeignClient(name = "temp-api", url = "http://localhost:8081")
interface TempApiFeignService {
    @PostMapping(value = ["/temp-api/process"])
    fun process(request: TempApiRequest): TempApiResponse
}