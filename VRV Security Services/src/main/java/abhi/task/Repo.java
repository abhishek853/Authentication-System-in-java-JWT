package abhi.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Repo extends JpaRepository<User, Long> {
	public User findByEmail(String email);	
	User findByResetToken(String token); 

}
