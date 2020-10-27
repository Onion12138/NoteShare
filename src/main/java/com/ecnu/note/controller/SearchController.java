package com.ecnu.note.controller;

import com.ecnu.note.service.SearchService;
import com.ecnu.note.vo.BaseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author onion
 * @date 2020/10/22 -10:28 上午
 */
@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;

    /**
    * @description: 搜索，使用es搜索引擎。返回一个map，有两个key。search:(NoteSearch对象), total:(long型表示搜索到的记录数)
    * @param:
    */
    @GetMapping("/search")
    public BaseResponseVO searchByKeyword(@RequestParam String keyword, @RequestParam(defaultValue = "1")Integer page) {
        Map<String, Object> map = searchService.findByKeyword(keyword, page);
        return BaseResponseVO.success(map);
    }
}
