package com.ecnu.note.controller;

import com.ecnu.note.domain.search.NoteSearch;
import com.ecnu.note.service.SearchService;
import com.ecnu.note.vo.BaseResponseVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author onion
 * @date 2020/10/22 -10:28 上午
 */
@RestController
public class SearchController {
    @Autowired
    private SearchService searchService;
    @GetMapping("/search")
    public BaseResponseVO searchByKeyword(@RequestParam String keyword, @RequestParam(defaultValue = "1")Integer page) {
        Page<NoteSearch> notes = searchService.findByKeyword(keyword, page);
        return BaseResponseVO.success(notes);
    }
}
