package fr.cours.weather.api.infra.in.exception;

public record ApiError(int status, String message) {
}
