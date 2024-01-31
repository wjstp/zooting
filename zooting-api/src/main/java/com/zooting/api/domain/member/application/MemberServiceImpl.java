package com.zooting.api.domain.member.application;

import com.zooting.api.domain.block.entity.Block;
import com.zooting.api.domain.member.dao.ExtractObj;
import com.zooting.api.domain.member.dao.MemberRepository;
import com.zooting.api.domain.member.dto.request.*;
import com.zooting.api.domain.member.dto.response.*;
import com.zooting.api.domain.member.entity.AdditionalInfo;
import com.zooting.api.domain.member.entity.Member;
import com.zooting.api.domain.member.entity.Privilege;
import com.zooting.api.global.common.code.ErrorCode;
import com.zooting.api.global.exception.BaseExceptionHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;
    public static final String DEFAULT_MASK = "https://zooting-s3-bucket.s3.ap-northeast-2.amazonaws.com/default_animal.png";
    public static final String DEFAULT_BACKGROUND = "https://zooting-s3-bucket.s3.ap-northeast-2.amazonaws.com/zooting-background-default.jpg";
    public static final Long DEFAULT_MASK_ID = 99L;
    public static final Long DEFAULT_BACKGROUND_ID = 99L;
    public static final Long DEFAULT_POINT = 0L;
    public static final Long CHANGE_NICKNAME_PRICE = 10L;
    @Override
    public boolean existNickname(String nickname) {
        return memberRepository.existsByNickname(nickname);
    }

    @Override
    public boolean checkMemberPrivilege(String userId) {
        Member member = memberRepository.findMemberByEmail(userId)
                .orElseThrow(() -> new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        for (var role : member.getRole()) {
            if (role.equals(Privilege.USER)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public MyProfileReq checkMyProfile(String userId, String nickname) {
        Member member = memberRepository.findMemberByEmail(userId)
                .orElseThrow(() -> new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        if (member.getNickname().equals(nickname) ) {
            return new MyProfileReq(true);
        }
        return new MyProfileReq(false);
    }

    @Transactional(readOnly = true)
    @Override
    public MemberRes findMemberInfo(String memberId) {
        Member member = memberRepository.findMemberByEmail(memberId).orElseThrow(() ->
                new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));

        return new MemberRes(
                member.getEmail(),
                member.getGender(),
                member.getNickname(),
                member.getBirth(),
                member.getAddress(),
                member.getPoint(),
                member.getAdditionalInfo().getIntroduce(),
                member.getAdditionalInfo().getPersonality(),
                member.getAdditionalInfo().getAnimal(),
                member.getAdditionalInfo().getInterest(),
                member.getAdditionalInfo().getIdealAnimal(),
                member.getAdditionalInfo().getBackgroundId(),
                member.getAdditionalInfo().getBackgroundUrl(),
                member.getAdditionalInfo().getMaskId(),
                member.getAdditionalInfo().getMaskUrl()
        );
    }

    @Override
    public MemberRes findMemberInfoByNickname(String nickname) {
        Member member = memberRepository.findMemberByNickname(nickname).orElseThrow(() ->
                new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        return new MemberRes(
                member.getEmail(),
                member.getGender(),
                member.getNickname(),
                member.getBirth(),
                member.getAddress(),
                null,
                member.getAdditionalInfo().getIntroduce(),
                member.getAdditionalInfo().getPersonality(),
                member.getAdditionalInfo().getAnimal(),
                member.getAdditionalInfo().getInterest(),
                member.getAdditionalInfo().getIdealAnimal(),
                member.getAdditionalInfo().getBackgroundId(),
                member.getAdditionalInfo().getBackgroundUrl(),
                member.getAdditionalInfo().getMaskId(),
                member.getAdditionalInfo().getMaskUrl()
        );
    }

    @Transactional
    @Override
    public void updateMemberInfo(String memberId, MemberReq memberReq) throws ParseException {
        Member member = memberRepository.findMemberByEmail(memberId).orElseThrow(() ->
                new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        if (existNickname(memberReq.nickname())) {
            throw new BaseExceptionHandler(ErrorCode.NOT_VALID_ERROR);
        }
        member.setNickname(memberReq.nickname());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        member.setBirth(sdf.parse(memberReq.birth()));
        member.setAddress(memberReq.address());
        member.setGender(memberReq.gender().toString());
        member.setPoint(DEFAULT_POINT); // 추가 정보 저장 시 포인트 0으로 저장

        AdditionalInfo additionalInfo = member.getAdditionalInfo();
        if (Objects.isNull(additionalInfo)) {
            additionalInfo = new AdditionalInfo();
        }
        additionalInfo.setInterest(memberReq.interest().toString());
        additionalInfo.setIdealAnimal(memberReq.idealAnimal().toString());

        // 디폴트 마스크, 배경 이미지로 저장
        additionalInfo.setMaskUrl(DEFAULT_MASK);
        additionalInfo.setBackgroundUrl(DEFAULT_BACKGROUND);
        additionalInfo.setMaskId(DEFAULT_MASK_ID);
        additionalInfo.setBackgroundId(DEFAULT_BACKGROUND_ID);
        additionalInfo.setMember(member);

        memberRepository.save(member);
    }

    @Transactional
    @Override
    public void updateMemberInfo(String memberId, MemberModifyReq memberModifyReq) {
        Member member = memberRepository.findMemberByEmail(memberId).orElseThrow(() ->
                new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));

        member.setAddress(memberModifyReq.address());
        member.getAdditionalInfo().setIdealAnimal(memberModifyReq.idealAnimal().toString());

        memberRepository.save(member);
    }

    @Transactional
    @Override
    public void updateInterests(String memberId, InterestsReq additionalReq) {
        Member member = memberRepository.findMemberByEmail(memberId)
                .orElseThrow(() -> new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        AdditionalInfo additionalInfo = member.getAdditionalInfo();
        if (Objects.isNull(additionalInfo)) {
            additionalInfo = new AdditionalInfo();
        }
        additionalInfo.setInterest(additionalReq.interest().toString());
        additionalInfo.setIdealAnimal(additionalReq.idealAnimal().toString());
        additionalInfo.setMember(member);
        memberRepository.save(member);
    }

    @Transactional
    @Override
    public void updateIntroduce(String memberId, IntroduceReq introduceReq) {
        Member member = memberRepository.findMemberByEmail(memberId)
                .orElseThrow(() -> new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        AdditionalInfo additionalInfo = member.getAdditionalInfo();
        if (Objects.isNull(additionalInfo)) {
            additionalInfo = new AdditionalInfo();
        }
        additionalInfo.setIntroduce(introduceReq.introduce());
        additionalInfo.setMember(member);
        memberRepository.save(member);
    }

    @Override
    public void changeMask(String memberId, MaskInventoryReq maskInventoryReq) {
        Member member = memberRepository.findMemberByEmail(memberId).orElseThrow(()->
                new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        for (var myMask : member.getMyMasks()) {
            if (myMask.getId() == maskInventoryReq.maskInventoryId()){
                member.getAdditionalInfo().setMaskId(myMask.getMask().getId());
                member.getAdditionalInfo().setMaskUrl(myMask.getMask().getFile().getImgUrl());
                memberRepository.save(member);
                return ;
            }
        }
    }

    @Override
    public void changeBackground(String memberId, BackgroundInventoryReq backgroundInventoryReq) {
        Member member = memberRepository.findMemberByEmail(memberId).orElseThrow(()->
                new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        for (var myBackground : member.getMyBackgrounds()) {
            if (myBackground.getId() == backgroundInventoryReq.backgroundInventoryId()){
                member.getAdditionalInfo().setBackgroundId(myBackground.getBackground().getId());
                member.getAdditionalInfo().setBackgroundUrl(myBackground.getBackground().getFile().getImgUrl());
                memberRepository.save(member);
                return ;
            }
        }
    }

    @Transactional
    @Override
    public boolean modifyNickname(String memberId, NicknameReq nicknameReq) {
        Member member = memberRepository.findMemberByEmail(memberId)
                .orElseThrow(() -> new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        // 닉네임 중복 체크 && 잔여 포인트 확인
        if (! memberRepository.existsByNickname(nicknameReq.nickname()) && member.getPoint() >= CHANGE_NICKNAME_PRICE) {
            // 닉네임 변경
            member.setNickname(nicknameReq.nickname());
            // 포인트 차감
            member.setPoint(member.getPoint()- CHANGE_NICKNAME_PRICE);
            memberRepository.save(member);
            return true;
        }
        return false;
    }


    @Transactional(readOnly = true)
    @Override
    public List<MemberSearchRes> findMemberList(String userId, String nickname) {
        Member member = memberRepository.findMemberByEmail(userId)
                .orElseThrow(() -> new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));

        // 나를 차단한 유저 리스트 추출
        List<Block> blockList = member.getBlockToList();
        List<Member> findMembers;

        if (!blockList.isEmpty()) {
            List<String> blockMemberNicknames = blockList.stream().map(block -> block.getFrom().getNickname()).toList();
            findMembers = memberRepository.findByNicknameContainingAndNicknameNotIn(nickname, blockMemberNicknames);
        } else {
            findMembers = memberRepository.findMemberByNicknameContaining(nickname);
        }
        return findMembers.stream().map(mem -> new MemberSearchRes(mem.getEmail(), mem.getNickname(),
                mem.getGender().toString(), mem.getAdditionalInfo().getAnimal())).toList();
    }

    @Transactional
    @Override
    public void updatePersonality(String userId, PersonalityReq personalityReq) {
        Member member = memberRepository.findMemberByEmail(userId)
                .orElseThrow(() -> new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        AdditionalInfo additionalInfo = member.getAdditionalInfo();
        if (Objects.isNull(additionalInfo)) {
            additionalInfo = new AdditionalInfo();
        }
        additionalInfo.setPersonality(personalityReq.personality());
        additionalInfo.setMember(member);
        memberRepository.save(member);
    }


    @Override
    public PointRes findPoints(String userId) {
        Member member = memberRepository.findMemberByEmail(userId)
                .orElseThrow(() -> new BaseExceptionHandler((ErrorCode.NOT_FOUND_USER)));
        return new PointRes(member.getPoint());
    }

    @Transactional
    @Override
    public Boolean deductPoints(String userId, Long price) {
        Member member = memberRepository.findMemberByEmail(userId)
                .orElseThrow(() -> new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        Long memberPoints = member.getPoint();
        if (memberPoints < price) {
            return false;
        }
        member.setPoint(memberPoints - price);
        memberRepository.save(member);
        return true;
    }
    @Override
    public List<MemberSearchRes> extractMembers(String userId, ExtractingReq extractingReq) {
        Member member = memberRepository.findMemberByEmail(userId)
                .orElseThrow(() -> new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        ExtractObj extractObj = new ExtractObj();
        extractObj.setUserId(userId);
        extractObj.setBlockToList(member.getBlockToList().stream().map(block-> block.getFrom().getEmail()).toList());
        extractObj.setBlockFromList(member.getBlockFromList().stream().map(block-> block.getTo().getEmail()).toList());
        extractObj.setFriendList(member.getFriendList().stream().map(fr-> fr.getFollowing().getEmail()).toList());
        extractObj.setMemberInterests(member.getAdditionalInfo().getInterest().lines().toList());
        extractObj.setMemberIdeals(member.getAdditionalInfo().getIdealAnimal().lines().toList());
        extractObj.setMemberBirth(member.getBirth());
        extractObj.setRangeYear(extractingReq.rangeYear());
        System.out.println(extractObj.getMemberIdeals());
        return memberRepository.extractMatchingMember(extractObj).stream().map(mem -> new MemberSearchRes(mem.getEmail(),mem.getNickname(), mem.getGender().toString(), mem.getAdditionalInfo().getAnimal())).toList();
    }

    @Override
    public List<MemberSearchRes> findMyBlockList(String userId) {
        Member member = memberRepository.findMemberByEmail(userId)
                .orElseThrow(() -> new BaseExceptionHandler(ErrorCode.NOT_FOUND_USER));
        return member.getBlockFromList().stream()
                .map(block -> new MemberSearchRes(block.getTo().getEmail(), block.getTo().getNickname()
                        ,block.getTo().getGender().toString(), block.getTo().getAdditionalInfo().getAnimal())).toList();


    }
}
