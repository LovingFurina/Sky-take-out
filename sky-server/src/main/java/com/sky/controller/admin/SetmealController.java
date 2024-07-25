package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Api(tags = "套餐相关接口")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    /**
     * 分页查询套餐
     *
     * @param setmealPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询套餐")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        log.info("分页查询套餐:{}", setmealPageQueryDTO);
        PageResult result = setmealService.pageQuery(setmealPageQueryDTO);

        return Result.success(result);
    }

    /**
     * 起售停售套餐
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("起售停售套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result startOrStop(@PathVariable Integer status, Long id) {
        log.info("{}套餐，id={}", status == 1 ? "起售" : "停售",id);
        setmealService.startOrStop(status, id);

        return Result.success();
    }

    /**
     * 修改套餐信息
     * @param setmealDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改套餐信息")
    public Result updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐信息:{}",setmealDTO);
        setmealService.updateSetmeal(setmealDTO);

        return Result.success();
    }

    /**
     * 条件查询
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类id查询套餐")

    public Result<List<Setmeal>> list(Long categoryId) {
        Setmeal setmeal = new Setmeal();
        setmeal.setCategoryId(categoryId);
        setmeal.setStatus(StatusConstant.ENABLE);

        List<Setmeal> list = setmealService.list(setmeal);
        return Result.success(list);
    }

    /**
     * 根据id查询套餐
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询套餐")
    public Result<SetmealVO> getDishById(@PathVariable Long id) {
        log.info("根据套餐id查询套餐");
        SetmealVO setmealVO= setmealService.getDishById(id);

        return Result.success(setmealVO);
    }

    /**
     * 根据id批量删除套餐
     * @param ids
     * @return
     */

    @DeleteMapping
    @ApiOperation("根据id批量删除套餐")
    @CacheEvict(cacheNames = "setmealCache",allEntries = true)
    public Result batchDeleteById(@RequestParam List<Long> ids){
        log.info("根据id批量删除套餐:{}",ids);
        setmealService.batchDeleteById(ids);
        return Result.success();
    }

    /**
     * 根据套餐id查询包含的菜品列表
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    @ApiOperation("根据套餐id查询包含的菜品列表")
    public Result<List<DishItemVO>> dishList(@PathVariable("id") Long id) {
        List<DishItemVO> list = setmealService.getDishItemById(id);
        return Result.success(list);
    }

    /**
     * 新增套餐
     * @param setmealDTO
     * @return
     */

    @PostMapping
    @ApiOperation("新增套餐")
    @CacheEvict(cacheNames = "setmealCache",key = "#setmealDTO.categoryId")//key:setmealCache::100
    public Result insert(@RequestBody SetmealDTO setmealDTO){

        log.info("新增套餐:{}",setmealDTO);
        setmealService.insert(setmealDTO);

        return Result.success();
    }


}
