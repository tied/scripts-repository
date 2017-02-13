
package com.onresolve.jira.groovy.jql

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.jql.query.QueryCreationContext
import com.atlassian.jira.jql.validator.NumberOfArgumentsValidator
import com.atlassian.jira.security.JiraAuthenticationContext
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.util.MessageSet
import com.atlassian.query.clause.TerminalClause
import com.atlassian.query.operand.FunctionOperand
import org.apache.lucene.index.Term
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery

class QueryEpics extends AbstractScriptedJqlFunction implements JqlQueryFunction {

    @Override
    Query getQuery(QueryCreationContext queryCreationContext, FunctionOperand operand, TerminalClause terminalClause) {
        JiraAuthenticationContext context = ComponentAccessor.getJiraAuthenticationContext()
        ApplicationUser applicationUser = context.getLoggedInUser()
        def booleanQuery = new BooleanQuery()

        // 1st pass query to get epics in the passed project
        def epics = getIssues("project = " + operand.args[0] + " AND issuetype = Epic", applicationUser)

        // 2nd pass query to get all the stories
        epics.each { Issue epic ->
            try {
                def epicID = epic.getKey()
                def stories = getIssues("\"Epic Link\" in (" + epicID + ")", applicationUser)

                stories.each { Issue story ->
                    def storyID = story.id as String
                    booleanQuery.add(new TermQuery(new Term("issue_id", storyID)), BooleanClause.Occur.SHOULD)
                }

            } catch (NullPointerException NPE) {

            }
        }
        return booleanQuery
    }

    @Override
    MessageSet validate(ApplicationUser user, FunctionOperand operand, TerminalClause terminalClause) {
        def messageSet = new NumberOfArgumentsValidator(1, 1, getI18n()).validate(operand)

        if (messageSet.hasAnyErrors()) {
            return messageSet
        }
    }

    @Override
    String getDescription() {
        "Query all contained stories in the project epics"
    }

    @Override
    List<Map> getArguments() {
        [
                [
                        description: "Project to query",
                        optional: false,
                ]
        ]
    }

    @Override
    String getFunctionName() {
        "_queryEpics"
    }
}
