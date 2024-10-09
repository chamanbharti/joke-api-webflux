package joke.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
@Table("joke")
public class Joke {
	@Id
    private Long id;
    private String setup;
    private String punchline;
   

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



