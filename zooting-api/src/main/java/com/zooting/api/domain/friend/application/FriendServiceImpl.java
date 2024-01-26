package com.zooting.api.domain.friend.application;

import com.zooting.api.domain.friend.dao.FriendRepository;
import com.zooting.api.domain.friend.dto.response.FriendRes;
import com.zooting.api.domain.friend.entity.Friend;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class FriendServiceImpl implements FriendService{

    private final FriendRepository friendRepository;
    @Override
    public List<FriendRes> getFriends(String follower) {
        List<Friend> friendList = friendRepository.findFriendByFollower(follower);
        return friendList
                .stream()
                .map(friend -> new FriendRes(
                        friend.getFollowing().getEmail(),
                        friend.getFollowing().getNickname(),
                        Optional.ofNullable(friend.getFollowing().getAdditionalInfo())
                                .map(additionalInfo -> additionalInfo.getAnimal())
                                .orElse(null),
                        friend.getFollowing().getGender()))
                .toList();
    }

    @Override
    public List<FriendRes> searchFriend(String nickname, String loginEmail){
        //search friend contating nickname
        return friendRepository.findByFollower_EmailAndFollowing_NicknameContaining
                        (loginEmail, nickname)
                .stream()
                .map(friend -> new FriendRes(
                        friend.getFollowing().getEmail(),
                        friend.getFollowing().getNickname(),
                        Optional.ofNullable(friend.getFollowing().getAdditionalInfo())
                                .map(additionalInfo -> additionalInfo.getAnimal())
                                .orElse(null),
                        friend.getFollowing().getGender()))
                .toList();
    }

}
