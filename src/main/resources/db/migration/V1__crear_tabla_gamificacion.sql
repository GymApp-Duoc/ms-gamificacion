CREATE TABLE perfiles_gamificacion (
                                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                       miembro_id BIGINT NOT NULL UNIQUE,
                                       puntos_totales INT NOT NULL DEFAULT 0,
                                       nivel INT NOT NULL DEFAULT 1,
                                       insignias_codigos VARCHAR(500) DEFAULT '',
                                       ultima_actividad TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);