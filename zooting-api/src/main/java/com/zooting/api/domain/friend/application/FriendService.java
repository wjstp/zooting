package com.zooting.api.domain.friend.application;

import com.zooting.api.domain.friend.dto.request.FriendReq;
import com.zooting.api.domain.friend.dto.response.FriendRes;
import com.zooting.api.domain.member.dto.response.MemberRes;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface FriendService {
    List<FriendRes> getFriends(String follower);
    void            acceptFriend(FriendReq friendReq, Authentication authentication);
    List<MemberRes> searchFriend(String nickname);


}
