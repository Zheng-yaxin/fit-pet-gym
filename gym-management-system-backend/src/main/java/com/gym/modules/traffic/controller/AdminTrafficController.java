package com.gym.modules.traffic.controller;

import com.gym.common.result.R;
import com.gym.modules.traffic.domain.entity.GymArea;
import com.gym.modules.traffic.domain.entity.TrafficSnapshot;
import com.gym.modules.traffic.service.IGymAreaService;
import com.gym.modules.traffic.service.ITrafficSnapshotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@Tag(name = "管理端人流")
@RestController
@RequestMapping("/admin/gym/traffic")
public class AdminTrafficController {
    @Autowired
    private IGymAreaService areaService;

    @Autowired
    private ITrafficSnapshotService snapshotService;

    @GetMapping("/areas")
    @Operation(summary = "查询健身房区域")
    public R<List<GymArea>> areas() {
        return R.ok(areaService.list());
    }

    @PostMapping("/areas")
    @Operation(summary = "新增健身房区域")
    public R<Void> addArea(@RequestBody GymArea area) {
        area.setStatus(area.getStatus() == null ? "0" : area.getStatus());
        return areaService.save(area) ? R.ok() : R.fail("新增区域失败");
    }

    @PostMapping("/snapshot")
    @Operation(summary = "新增人流快照")
    public R<Void> snapshot(@RequestBody TrafficSnapshot snapshot) {
        snapshot.setSnapshotTime(snapshot.getSnapshotTime() == null ? new Date() : snapshot.getSnapshotTime());
        if (snapshot.getHeatLevel() == null && snapshot.getCapacity() != null && snapshot.getCapacity() > 0) {
            snapshot.setHeatLevel(Math.min(100, snapshot.getCurrentCount() * 100 / snapshot.getCapacity()));
        }
        return snapshotService.save(snapshot) ? R.ok() : R.fail("新增人流快照失败");
    }
}
