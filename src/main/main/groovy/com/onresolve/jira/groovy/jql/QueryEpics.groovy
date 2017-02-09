package com.onresolve.jira.groovy.jql

import com.atlassian.jira.jql.query.QueryCreationContext
import com.atlassian.query.clause.TerminalClause
import com.atlassian.query.operand.FunctionOperand
import groovy.util.logging.Log4j
import org.apache.lucene.search.MatchAllDocsQuery
import org.apache.lucene.search.Query

import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.web.bean.PagerFilter

@Log4j
class QueryEpics extends AbstractScriptedJqlFunction implements JqlQueryFunction {

    // Execute a JQL query
    def findIssues(String jqlQuery) {
        def issueManager = ComponentAccessor.issueManager
        def user = ComponentAccessor.jiraAuthenticationContext.getLoggedInUser()
        def jqlQueryParser = ComponentAccessor.getComponent(JqlQueryParser.class)
        def searchProvider = ComponentAccessor.getComponent(SearchProvider.class)

        def query = jqlQueryParser.parseQuery(jqlQuery)
        def results = searchProvider.search(query, user, PagerFilter.unlimitedFilter)
        results.issues.collect { issue -> issueManager.getIssueObject(issue.id) }
    }

    @Override
    Query getQuery(QueryCreationContext queryCreationContext, FunctionOperand operand, TerminalClause terminalClause) {
        new MatchAllDocsQuery()

        // Define the 1st pass query and execute
        def epicQuery = "project = scrum and issuetype = epic"
        def epics = findIssues(epicQuery)

        // Parse out the results and build a new JQL query from it
        StringBuilder storyQuery = new StringBuilder()

        for ( int i = 0; i < epics.size(); i++) {

            if ( i == 0 ) {
                storyQuery.append('"Epic Link" in (')
            }

            storyQuery.append(epics.get(i))

            if ( i != epics.size() - 1 ) {
                storyQuery.append(", ")
            } else {
                storyQuery.append(")")
            }
        }

        // Execute the 2nd pass query
        def stories = findIssues(storyQuery.toString())

        return stories
    }

    @Override
    String getDescription() {
        "Query all contained stories in the project epics"
    }

    @Override
    List<Map> getArguments() {
        []
    }

    @Override
    String getFunctionName() {
        "queryEpics"
    }
}