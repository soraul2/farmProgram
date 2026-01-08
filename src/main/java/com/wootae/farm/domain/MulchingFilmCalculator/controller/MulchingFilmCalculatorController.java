package com.wootae.farm.domain.MulchingFilmCalculator.controller;

import com.wootae.farm.domain.MulchingFilmCalculator.dto.MulchingFilmDTO;
import com.wootae.farm.domain.MulchingFilmCalculator.service.MulchingFilmCalculatorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession; // [추가] 세션 사용
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MulchingFilmCalculatorController {

    private final MulchingFilmCalculatorService mulchingFilmCalculatorService;

    @GetMapping({"/", "/mulchingFilmCalculator"})
    public String showMulchingFilmCalculator(Model model, HttpSession session) {
        model.addAttribute("request", new MulchingFilmDTO.Request());

        // 처음 들어왔을 때도 기존 기록이 있다면 보여주기 위해 session에서 꺼내서 model에 담음
        List<MulchingFilmDTO.Record> history = (List<MulchingFilmDTO.Record>) session.getAttribute("history");
        model.addAttribute("history", history); // 없으면 null이 들어감 (화면에서 처리 가능)

        return "Mulching-filmCalculator";
    }

    @PostMapping("/mulchingFilmCalculator")
    @Operation(summary = "비닐 수량 계산하기", description = "평수 , 폭 , 길이 , 가격을 입력받아 롤 개수와 비용을 계산합니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "계산 성공",
                    content = @Content(schema = @Schema(implementation = MulchingFilmDTO.Response.class))
            )
    })
    public String mulchingFilmCalculator(@Valid @ModelAttribute("request") MulchingFilmDTO.Request request,
                                         BindingResult bindingResult,
                                         Model model,
                                         HttpSession session) { // [중요] HttpSession 주입 받기

        // 1. 세션에서 기존 기록 리스트 가져오기
        List<MulchingFilmDTO.Record> history = (List<MulchingFilmDTO.Record>) session.getAttribute("history");

        // 2. 만약 기록이 하나도 없다면? 새 리스트 생성
        if (history == null) {
            history = new ArrayList<>();
        }

        if (bindingResult.hasErrors()) {
            // 에러 나도 기존 기록은 계속 보여줘야 함
            model.addAttribute("history", history);
            return "Mulching-filmCalculator";
        }

        // 3. 계산 수행
        MulchingFilmDTO.Response response = mulchingFilmCalculatorService.mulchingFilmCalculator(request);

        // 4. [핵심] 기록 생성 및 리스트에 추가
        // 0번 인덱스에 추가하면 가장 최신 기록이 맨 위로 옴!
        history.add(0, new MulchingFilmDTO.Record(request, response));

        // 5. 너무 많이 쌓이면 지저분하니까 최근 5개만 유지 (선택사항)
        if (history.size() > 5) {
            history.remove(history.size() - 1);
        }

        // 6. 업데이트된 리스트를 다시 세션에 저장
        session.setAttribute("history", history);

        // 7. 화면에 전달
        model.addAttribute("response", response);
        model.addAttribute("history", history); // 리스트 전달

        return "Mulching-filmCalculator";
    }
}