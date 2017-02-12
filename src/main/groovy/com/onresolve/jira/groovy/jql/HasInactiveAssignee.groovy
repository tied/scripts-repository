package com.onresolve.jira.groovy.jql

import com.atlassian.jira.ComponentManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.search.SearchProvider
import com.atlassian.jira.jql.operand.QueryLiteral
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.jql.query.RangeQueryFactory
import com.atlassian.jira.util.MessageSetImpl
import com.atlassian.jira.web.bean.PagerFilter
import com.atlassian.query.operand.FunctionOperand
import com.atlassian.query.operator.Operator
import org.apache.log4j.Category
import com.atlassian.jira.util.MessageSet
import com.atlassian.crowd.embedded.api.User
import com.atlassian.query.clause.TerminalClause
import com.atlassian.jira.jql.query.QueryCreationContext
import org.apache.lucene.document.NumberTools
import org.apache.lucene.index.Term
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import com.onresolve.jira.groovy.jql.AbstractScriptedJqlFunction
import org.apache.lucene.search.BooleanClause
import org.apache.lucene.search.BooleanQuery
import org.apache.lucene.search.Query
import org.apache.lucene.search.TermQuery
import com.atlassian.jira.issue.fields.CustomField
import com.atlassian.jira.issue.CustomFieldManager
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.security.JiraAuthenticationContext;



class HasInactiveAssignee extends AbstractScriptedJqlFunction implements JqlQueryFunction{
    @Override
    String getDescription() {
        "Function to show only the inactive users"
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
        "_hasInactiveAssignee"
    }

    def String subquery
    //@Override
    MessageSet validate(User user, FunctionOperand operand, TerminalClause terminalClause) {
        def messageSet = new MessageSetImpl()
        return messageSet
    }

    @Override
    Query getQuery(QueryCreationContext queryCreationContext, FunctionOperand operand, TerminalClause terminalClause) {
        //User user=ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser();
        JiraAuthenticationContext context = ComponentAccessor.getJiraAuthenticationContext();
        ApplicationUser applicationUser = context.getUser();
        def booleanQuery = new BooleanQuery()
        issues = getIssues(operand.args[0], applicationUser)
        issues.each {Issue issue ->
            try{
                def active = issue.assignee.isActive()
                if ( !active )
                    booleanQuery.add(new TermQuery(new Term("issue_id", issue.id as String)), BooleanClause.Occur.SHOULD)
            }catch(NullPointerException NPE){

            }
        }

        return booleanQuery
    }
}