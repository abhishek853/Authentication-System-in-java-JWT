package net.codejava;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.Rollback;

import abhi.task.Repo;
import abhi.task.User;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Rollback(false)
public class RepoTests {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private Repo repo;



	@Test
	public void testFindByEmail() {
		// Use a unique email for testing
		String email = "john.doe@gmail.com";

		// Check if the user already exists to avoid duplication
		User existingUser = repo.findByEmail(email);
		if (existingUser == null) {
			// Create a new user if it does not exist
			User user = new User();
			user.setEmail(email);
			user.setPassword("password123");
			user.setFirstName("John");
			user.setLastName("Doe");
			user.setAddress("123 Main St");
			user.setCity("Sample City");
			user.setState("Sample State");
			user.setDateOfBirth("1990-01-01"); // Using String for DOB as defined in your User class
			user.setGender("Male");

			repo.save(user); // Save the user to the database
		}

		// Now try to find the user by email
		User foundUser = repo.findByEmail(email);

		assertThat(foundUser).isNotNull(); // Check that the user is found
		assertThat(foundUser.getEmail()).isEqualTo(email);
	}
}
