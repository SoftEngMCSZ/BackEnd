package me.whatdo.app.entitymodel;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;

import java.nio.charset.Charset;
import java.util.Objects;

public class Collaborator {
	private static final int HASH_ITERATION_COUNT = 4;
	private static final int HASH_MEM = 1024 * 1024;
	private static final int HASH_DEG_PARALLELISM = 8;

	// Guaranteed non-null
	private final String name;
	// May be null if Collaborator registered without password. Don't serialize this
	private final String password;

	public Collaborator(String name) {
		this.name = name;
		this.password = null;
	}

	public String getName() {
		return this.name;
	}

	public Collaborator(String name, String pwd) {
		this.name = name;

		Argon2 hasher = Argon2Factory.create(Argon2Types.ARGON2id);
		this.password = hasher.hash(
				HASH_ITERATION_COUNT,
				HASH_MEM,
				HASH_DEG_PARALLELISM,
				pwd.toCharArray(),
				Charset.defaultCharset()
		);
	}

	public boolean verifyPassword(String pwd) {
		if(this.password == null) return true;
		Argon2 hasher = Argon2Factory.create(Argon2Types.ARGON2id);
		return hasher.verify(this.password,pwd.toCharArray());
	}

	public int hashCode() {
		return Objects.hash(this.name,this.password);
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		Collaborator that = (Collaborator) o;
		return this.name.equals(that.name) && Objects.equals(this.password, that.password);
	}
}
