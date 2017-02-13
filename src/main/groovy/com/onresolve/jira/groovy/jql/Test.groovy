
package com.onresolve.jira.groovy.jql

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.jql.query.QueryCreationContext
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.util.MessageSet
import com.atlassian.jira.util.MessageSetImpl
import com.atlassian.query.clause.TerminalClause
import com.atlassian.query.operand.FunctionOperand
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery

class Test extends AbstractScriptedJqlFunction implements JqlQueryFunction {
    @Override
    String getDescription() {
        "Test JQL function"
    }

    @Override
    List<Map> getArguments() {
        [
                [
                        "description": "Subquery",
                        "optional": false,
                ]
        ]
    }

    @Override
    String getFunctionName() {
        "_testFunction"
    }

    @Override
    MessageSet validate(ApplicationUser user, FunctionOperand operand, TerminalClause terminalClause) {
        def messageSet = new MessageSetImpl()
        return messageSet
    }

    @Override
    Query getQuery(QueryCreationContext queryCreationContext, FunctionOperand operand, TerminalClause terminalClause) {
        JiraAuthenticationContext context = ComponentAccessor.getJiraAuthenticationContext()
        ApplicationUser applicationUser = context.getLoggedInUser()
        def booleanQuery = new BooleanQuery()
        issues = getIssues(operand.args[0], applicationUser)
        issues.each {Issue issue ->
            try {
                booleanQuery.add(new TermQuery(new Term("issue_id", issue.id as String)), BooleanClause.Occur.SHOULD)
            } catch (NullPointerException NPE) {

            }
        }

        return booleanQuery
    }
}
