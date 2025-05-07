package com.ruoyi.web.controller.music;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.TimeSlot;
import com.ruoyi.system.service.ITimeSlotService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 时间段Controller
 * @date 2025-04-08
 */
@RestController
@RequestMapping("/slot")
public class TimeSlotController extends BaseController
{
    @Autowired
    private ITimeSlotService timeSlotService;

    /**
     * 查询时间段列表
     */
    @PreAuthorize("@ss.hasPermi('system:slot:list')")
    @GetMapping("/list")
    public TableDataInfo list(TimeSlot timeSlot)
    {
        startPage();
        List<TimeSlot> list = timeSlotService.selectTimeSlotList(timeSlot);
        return getDataTable(list);
    }

    /**
     * 导出时间段列表
     */
    @PreAuthorize("@ss.hasPermi('system:slot:export')")
    @Log(title = "时间段", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, TimeSlot timeSlot)
    {
        List<TimeSlot> list = timeSlotService.selectTimeSlotList(timeSlot);
        ExcelUtil<TimeSlot> util = new ExcelUtil<TimeSlot>(TimeSlot.class);
        util.exportExcel(response, list, "时间段数据");
    }

    /**
     * 获取时间段详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:slot:query')")
    @GetMapping(value = "/{slotId}")
    public AjaxResult getInfo(@PathVariable("slotId") Long slotId)
    {
        return success(timeSlotService.selectTimeSlotBySlotId(slotId));
    }

    /**
     * 新增时间段
     */
    @PreAuthorize("@ss.hasPermi('system:slot:add')")
    @Log(title = "时间段", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody TimeSlot timeSlot)
    {
        return toAjax(timeSlotService.insertTimeSlot(timeSlot));
    }

    /**
     * 修改时间段
     */
    @PreAuthorize("@ss.hasPermi('system:slot:edit')")
    @Log(title = "时间段", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody TimeSlot timeSlot)
    {
        return toAjax(timeSlotService.updateTimeSlot(timeSlot));
    }

    /**
     * 删除时间段
     */
    @PreAuthorize("@ss.hasPermi('system:slot:remove')")
    @Log(title = "时间段", businessType = BusinessType.DELETE)
	@DeleteMapping("/{slotIds}")
    public AjaxResult remove(@PathVariable Long[] slotIds)
    {
        return toAjax(timeSlotService.deleteTimeSlotBySlotIds(slotIds));
    }
}
