package allogica.trackingTimeDesktopApp.model.entity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Atividade {
	private String nome;
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	private LocalDateTime inicio;
	private LocalDateTime fim;
	private Map<Integer, Atividade> subatividades;

	public Atividade(String nome) {
		this.nome = nome;
		this.subatividades = new HashMap<>();
	}

	public void adicionarSubatividade(int id, Atividade subatividade) {
		subatividades.put(id, subatividade);
	}

	public Duration calcularTempoTotal() {
		Duration tempoTotal = Duration.ZERO;
		if ((inicio != null && fim != null) && this.subatividades.isEmpty()) {
			return tempoTotal = Duration.between(inicio, fim);
		} else {
			for (Atividade subatividade : subatividades.values()) {
				tempoTotal = tempoTotal.plus(subatividade.calcularTempoTotal());
			}
			return tempoTotal;
		}
	}

}
/*
 * CREATE TABLE Atividade ( id INT PRIMARY KEY, nome VARCHAR(100), descricao
 * TEXT, id_atividade_pai INT, -- Referência para a atividade pai FOREIGN KEY
 * (id_atividade_pai) REFERENCES Atividade(id) );
 */