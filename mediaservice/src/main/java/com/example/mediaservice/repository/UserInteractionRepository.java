package com.example.mediaservice.repository;
import com.example.mediaservice.model.Playlist;
import com.example.mediaservice.model.UserInteraction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserInteractionRepository extends JpaRepository<UserInteraction, Integer> {
    List<UserInteraction> findByUserUserId(Integer userId);
}