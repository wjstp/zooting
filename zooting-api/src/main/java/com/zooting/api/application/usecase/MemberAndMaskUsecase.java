package com.zooting.api.application.usecase;

import com.zooting.api.application.dto.request.MemberAndMaskReq;
import com.zooting.api.application.dto.response.MemberAndMaskRes;
import com.zooting.api.domain.mask.dao.MaskInventoryRepository;
import com.zooting.api.domain.mask.dao.MaskRepository;
import com.zooting.api.domain.mask.entity.Mask;
import com.zooting.api.domain.mask.entity.MaskInventory;
import com.zooting.api.domain.member.dao.MemberRepository;
import com.zooting.api.domain.member.entity.Member;
import com.zooting.api.global.common.code.ErrorCode;
import com.zooting.api.global.exception.BaseExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberAndMaskUsecase {
    final private MemberRepository memberRepository;
    final private MaskRepository maskRepository;
    final private MaskInventoryRepository maskInventoryRepository;
    public static final Long DEFAULT_MASK_ID = 99L;

    @Transactional
    public Boolean buyMask(String userId, MemberAndMaskReq maskReq) {
        Member member = memberRepository.findMemberByEmail(userId)
                .orElseThrow(()->new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        Long memberPoints = member.getPoint();
        Mask mask = maskRepository.findById(maskReq.maskId())
                .orElseThrow(()-> new BaseExceptionHandler(ErrorCode.NOT_FOUND_ERROR));

        // 이미 샀거나 가격보다 포인트가 적거나 자신의 동물상이 아니라 return false
        Boolean isInMaskInventory = maskInventoryRepository.existsByMaskIdAndMember(maskReq.maskId(), member);
        if (isInMaskInventory || memberPoints < mask.getPrice() || ! member.getAdditionalInfo().getAnimal().equals(mask.getAnimal())) {
            return false;
        }
        // 포인트 차감
        member.setPoint(memberPoints - mask.getPrice());
        memberRepository.save(member);
        // 인벤토리 추가
        MaskInventory maskInventory = new MaskInventory();
        maskInventory.setMask(mask);
        maskInventory.setMember(member);
        maskInventoryRepository.save(maskInventory);
        return true;

    }

    @Transactional
    public List<MemberAndMaskRes> findAllMaskInventory(String userId) {
        Member member = memberRepository.findMemberByEmail(userId).orElseThrow(()->
                new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        return maskInventoryRepository.findAllByMember(member)
                .stream().map(myMask-> new MemberAndMaskRes(
                        myMask.getId(),
                        myMask.getMask().getAnimal(),
                        myMask.getMask().getDescription(),
                        myMask.getMask().getPrice(),
                        myMask.getMask().getFile().getFileName(),
                        myMask.getMask().getFile().getImgUrl()
                         )).toList();
    }

}
