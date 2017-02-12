
package com.onresolve.jira.groovy.jql

import com.atlassian.jira.jql.query.QueryCreationContext
import com.atlassian.query.clause.TerminalClause
import com.atlassian.query.operand.FunctionOperand
import com.atlassian.jira.user.ApplicationUser
import groovy.util.logging.Log4j
import org.apache.lucene.search.Query
import com.atlassian.jira.util.*
import org.apache.lucene.queryParser.QueryParser

import java.text.MessageFormat

@Log4j
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

    def String subquery
    @Override
    MessageSet validate(ApplicationUser user, FunctionOperand operand, TerminalClause terminalClause) {
        def messageSet = new MessageSetImpl()
        return messageSet
    }

    @Override
    Query getQuery(QueryCreationContext queryCreationContext, FunctionOperand operand, TerminalClause terminalClause) {

        MessageFormat queryStr = new MessageFormat("project = scrum")
        def query = queryParser.parseQuery(queryStr)
//        luceneQueryBuilder.createLuceneQuery(queryCreationContext, query.whereClause)
    }
}
