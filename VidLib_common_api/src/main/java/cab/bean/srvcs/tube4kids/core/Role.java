package cab.bean.srvcs.tube4kids.core;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.ArrayUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "role")
@NoArgsConstructor
@Data
@EqualsAndHashCode(of = {"name"})
public class Role  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @JsonIgnore
    @Transient
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "roles", targetEntity = User.class)
    private Set<User> users = new HashSet<User>();

    public Role(String name) {
	this.name=name;
    }

    // Don't need this in beancab commons...
    public interface Names {

	public static final String SUDO_ROLE					=	"sudo";
	public static final String ADMIN_ROLE					=	"admin";
	public static final String USER_MANAGER_ROLE			=	"user_manager";
	public static final String ROLE_MANAGER_ROLE			=	"role_editor";
	public static final String VIDEO_MANAGER_ROLE			=	"video_manager";
	public static final String PLAYLIST_MANAGER_ROLE		=	"playlist_manager";
	public static final String UI_MANAGER_ROLE				=	"ui_manager";
	public static final String CONTENT_MODERATOR_ROLE	=	"content_moderator";
	
	public static final String GUARDIAN_ROLE				=	"guardian";
	public static final String CHILD_EDIT_ROLE				=	"edit_child";
	public static final String MEMBER_ROLE				=	"member";
	public static final String PLAYLIST_EDIT_ROLE			=	"edit_playlist";
	public static final String VIDEO_EDIT_ROLE				=	"edit_video";
	public static final String CHILD_ROLE					=	"child";


	public static final String[] MEMBER			= new String[]{ VIDEO_EDIT_ROLE, PLAYLIST_EDIT_ROLE, MEMBER_ROLE };

	public static final String[] GUARDIAN		= ArrayUtils.addAll( MEMBER, new String[]{ CHILD_EDIT_ROLE, GUARDIAN_ROLE } );
	
	public static final String[] PLAYLIST_MANAGER	= new String[]{ ADMIN_ROLE, VIDEO_MANAGER_ROLE, PLAYLIST_MANAGER_ROLE};
	
	public static final String[] CONTENT_EDITOR		= ArrayUtils.addAll(PLAYLIST_MANAGER, new String[]{ UI_MANAGER_ROLE , CONTENT_MODERATOR_ROLE });
	
	public static final String[] USER_MANAGER	= new String[]{ ADMIN_ROLE, ROLE_MANAGER_ROLE, USER_MANAGER_ROLE, CHILD_EDIT_ROLE };

	public static final String[] ADMIN			= ArrayUtils.addAll(ArrayUtils.addAll( CONTENT_EDITOR, USER_MANAGER ), PLAYLIST_MANAGER);

	public static final String[] SUDO			= ArrayUtils.add(ADMIN, SUDO_ROLE);

    }

}

