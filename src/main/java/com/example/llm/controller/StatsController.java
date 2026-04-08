package com.example.llm.controller;

import com.example.llm.common.Result;
import com.example.llm.service.stats.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Tag(name = "节流统计", description = "Token 消耗与节省情况统计")
@RestController
@RequestMapping("/v1/proxy/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @Operation(summary = "获取全局 Token 节省统计报告")
    @GetMapping("/overall")
    public Result<Map<String, Object>> getOverallStats() {
        return Result.success(statsService.getOverallStats());
    }
}
