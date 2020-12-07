package me.whatdo.app.model.entity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;
import de.mkammerer.argon2.Argon2Factory.Argon2Types;

import java.nio.charset.Charset;
import java.util.Objects;
import java.util.UUID;

/**
 * A Collaborator registered for a single choice
 */
public class Collaborator {
	// Constants used in password hashing
	private static final int HASH_ITERATION_COUNT = 4;
	private static final int HASH_MEM = 512;
	private static final int HASH_DEG_PARALLELISM = 8;

	private static final Gson gson = new GsonBuilder().disableHtmlEscaping().create();

	private final UUID id;
	private final String name;
	// May be null if Collaborator registered without password. Don't serialize this
	private final transient String password;

	public Collaborator(UUID id, String name) {
		this(id, name, null);
	}

	public Collaborator(UUID id, String name, String pwd) {
		this.id = id;
		this.name = name;
		this.password = pwd;
	}

	public Collaborator(String name) {
		this(UUID.randomUUID(), name, null);
	}

	public Collaborator(String name, String pwd) {
		this(UUID.randomUUID(), name, pwd);
	}

	public static Collaborator fromPlaintextPassword(String name, String pwd) {
		Argon2 hasher = Argon2Factory.create(Argon2Types.ARGON2id);
		return new Collaborator(
				name,
				hasher.hash(
						HASH_ITERATION_COUNT,
						HASH_MEM,
						HASH_DEG_PARALLELISM,
						pwd.toCharArray(),
						Charset.defaultCharset()
				)
		);
	}

	public static Collaborator fromJson(String json) {
		return gson.fromJson(json, Collaborator.class);
	}

	public String getName() {
		return this.name;
	}

	public UUID getId() {
		return id;
	}

	public String toJson() {
		return gson.toJson(this);
	}

	public JsonObject toJsonObject() {
		return gson.fromJson(this.toJson(), JsonObject.class);
	}

	public boolean verifyPassword(String pwd) {
		if (this.password == null) return true;
		Argon2 hasher = Argon2Factory.create(Argon2Types.ARGON2id);
		return hasher.verify(this.password, pwd.toCharArray());
	}

	public String getPassword() {
		return this.password;
	}

	public int hashCode() {
		return Objects.hash(this.id);
	}

	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || o.getClass() != this.getClass()) return false;
		Collaborator that = (Collaborator) o;
		return this.id.equals(that.id);
	}
}
