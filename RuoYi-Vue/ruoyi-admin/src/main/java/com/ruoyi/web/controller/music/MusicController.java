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
import com.ruoyi.system.domain.Music;
import com.ruoyi.system.service.IMusicService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 音乐Controller
 * 
 * @author ruoyi
 * @date 2025-04-08
 */
@RestController
@RequestMapping("/music")
public class MusicController extends BaseController
{
    @Autowired
    private IMusicService musicService;

    /**
     * 查询音乐列表
     */
    @PreAuthorize("@ss.hasPermi('system:music:list')")
    @GetMapping("/list")
    public TableDataInfo list(Music music)
    {
        startPage();
        List<Music> list = musicService.selectMusicList(music);
        return getDataTable(list);
    }

    /**
     * 导出音乐列表
     */
    @PreAuthorize("@ss.hasPermi('system:music:export')")
    @Log(title = "音乐", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Music music)
    {
        List<Music> list = musicService.selectMusicList(music);
        ExcelUtil<Music> util = new ExcelUtil<Music>(Music.class);
        util.exportExcel(response, list, "音乐数据");
    }

    /**
     * 获取音乐详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:music:query')")
    @GetMapping(value = "/{musicId}")
    public AjaxResult getInfo(@PathVariable("musicId") Long musicId)
    {
        return success(musicService.selectMusicByMusicId(musicId));
    }

    /**
     * 新增音乐
     */
    @PreAuthorize("@ss.hasPermi('system:music:add')")
    @Log(title = "音乐", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Music music)
    {
        return toAjax(musicService.insertMusic(music));
    }

    /**
     * 修改音乐
     */
    @PreAuthorize("@ss.hasPermi('system:music:edit')")
    @Log(title = "音乐", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Music music)
    {
        return toAjax(musicService.updateMusic(music));
    }

    /**
     * 删除音乐
     */
    @PreAuthorize("@ss.hasPermi('system:music:remove')")
    @Log(title = "音乐", businessType = BusinessType.DELETE)
	@DeleteMapping("/{musicIds}")
    public AjaxResult remove(@PathVariable Long[] musicIds)
    {
        return toAjax(musicService.deleteMusicByMusicIds(musicIds));
    }
}
