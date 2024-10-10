package joke.model;

import jakarta.persistence.*;

@Entity
public class Joke {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String type;
    private String setup;
    private String punchline;
    
	
	public Joke(Long id, String type, String setup, String punchline) {
		super();
		this.id = id;
		this.type = type;
		this.setup = setup;
		this.punchline = punchline;
	}
	public Joke() {
		// TODO Auto-generated constructor stub
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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



