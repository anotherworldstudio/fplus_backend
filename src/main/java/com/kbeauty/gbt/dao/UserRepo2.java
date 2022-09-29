package com.kbeauty.gbt.dao;

import com.kbeauty.gbt.entity.domain.User;
import com.kbeauty.gbt.entity.domain.User2;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepo2 extends CrudRepository<User2, Long>{
	
	User2 findByUserId(String userId);
//	User2 findByEmail(String email);
	Optional<User2> findByEmail(String email);

	User2 findByFriendId(String friendId);


	boolean existsByEmail(String email);


}
