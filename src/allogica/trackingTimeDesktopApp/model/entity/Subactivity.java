package allogica.trackingTimeDesktopApp.model.entity;

import javax.persistence.*;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class Subactivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "activity_id")
    private Activity activity;

    @Column(name = "time")
    private LocalDateTime time;

    public Long getId() {
        return id;
    }

    public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}