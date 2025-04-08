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
import com.ruoyi.system.domain.SlotMusic;
import com.ruoyi.system.service.ISlotMusicService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 时间段音乐关联Controller
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@RestController
@RequestMapping("/slotMusic")
public class SlotMusicController extends BaseController
{
    @Autowired
    private ISlotMusicService slotMusicService;

    /**
     * 查询时间段音乐关联列表
     */
    @PreAuthorize("@ss.hasPermi('system:music:list')")
    @GetMapping("/list")
    public TableDataInfo list(SlotMusic slotMusic)
    {
        startPage();
        List<SlotMusic> list = slotMusicService.selectSlotMusicList(slotMusic);
        return getDataTable(list);
    }

    /**
     * 导出时间段音乐关联列表
     */
    @PreAuthorize("@ss.hasPermi('system:music:export')")
    @Log(title = "时间段音乐关联", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SlotMusic slotMusic)
    {
        List<SlotMusic> list = slotMusicService.selectSlotMusicList(slotMusic);
        ExcelUtil<SlotMusic> util = new ExcelUtil<SlotMusic>(SlotMusic.class);
        util.exportExcel(response, list, "时间段音乐关联数据");
    }

    /**
     * 获取时间段音乐关联详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:music:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(slotMusicService.selectSlotMusicById(id));
    }

    /**
     * 新增时间段音乐关联
     */
    @PreAuthorize("@ss.hasPermi('system:music:add')")
    @Log(title = "时间段音乐关联", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SlotMusic slotMusic)
    {
        return toAjax(slotMusicService.insertSlotMusic(slotMusic));
    }

    /**
     * 修改时间段音乐关联
     */
    @PreAuthorize("@ss.hasPermi('system:music:edit')")
    @Log(title = "时间段音乐关联", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SlotMusic slotMusic)
    {
        return toAjax(slotMusicService.updateSlotMusic(slotMusic));
    }

    /**
     * 删除时间段音乐关联
     */
    @PreAuthorize("@ss.hasPermi('system:music:remove')")
    @Log(title = "时间段音乐关联", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(slotMusicService.deleteSlotMusicByIds(ids));
    }
}
