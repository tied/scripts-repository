import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.security.roles.ProjectRoleManager
import com.atlassian.jira.user.ApplicationUsers

def commentManager = ComponentAccessor.getCommentManager()
def comments = commentManager.getComments(issue)
def projectRoleId = 10700  // ID of external-comments role

if (comments) {
    comments.last().body || \n—\n.*|\n--\n.*|\n---\n.*|On .*wrote:|----Orig.*|On .*(JIRA).*|.*(JIRA).*wrote:
}