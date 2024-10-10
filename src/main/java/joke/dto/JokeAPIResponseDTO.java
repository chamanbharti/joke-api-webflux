package joke.dto;

public class JokeAPIResponseDTO {
    private String type;
    private String setup;
    private String punchline;
    
	public JokeAPIResponseDTO() {
		super();
	}
	public JokeAPIResponseDTO(String type, String setup, String punchline) {
		super();
		this.type = type;
		this.setup = setup;
		this.punchline = punchline;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSetup() {
		return setup;
	}
	public void setSetup(String setup) {
		this.setup = setup;
	}
	public String getPunchline() {
		return punchline;
	}
	public void setPunchline(String punchline) {
		this.punchline = punchline;
	}

    
}

