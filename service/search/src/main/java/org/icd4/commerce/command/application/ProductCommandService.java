package org.icd4.commerce.command.application;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCommandService {
    private final ProductRegisterService productRegisterService;
    private final ProductModifierService productModifierService;
    //상품데이터 추가
    //상품데이터 수정
    //상품데이터 삭제
}
