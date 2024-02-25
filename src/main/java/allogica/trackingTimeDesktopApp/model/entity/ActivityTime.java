package allogica.trackingTimeDesktopApp.model.entity;

import javax.persistence.*;

import java.time.LocalDateTime;

@MappedSuperclass
public class ActivityTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    public Long getId() {
        return id;
    }
    
    
    
    @Column(name = "time")
    private LocalDateTime time;
    public LocalDateTime getTime() {
        return time;
    }
    public void setTime(LocalDateTime time) {
        this.time = time;
    }
    
    
    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;
    public Activity getActivity() {
		return activity;
	}
	public void setActivity(Activity activity) {
		this.activity = activity;
	}
    
	
	
    
}