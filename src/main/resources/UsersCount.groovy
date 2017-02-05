import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.util.UserManager

def userManager = ComponentAccessor.getUserManager() as UserManager
def usersCount = userManager.getAllUsers().size()

return "My instance contains " + usersCount + " users"