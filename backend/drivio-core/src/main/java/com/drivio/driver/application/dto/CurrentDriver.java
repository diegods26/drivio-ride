package com.drivio.driver.application.dto;

import com.drivio.iam.domain.valueobject.Cpf;

import java.util.UUID;

public record CurrentDriver(UUID id, Cpf cpf) {

	@Override
	public String toString() {
		return "CurrentDriver[id=" + id + ", cpf=REDACTED]";
	}
}