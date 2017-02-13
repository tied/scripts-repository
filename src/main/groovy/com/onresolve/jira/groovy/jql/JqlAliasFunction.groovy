package com.onresolve.jira.groovy.jql

import com.atlassian.jira.bc.issue.search.SearchService
import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.jql.query.LuceneQueryBuilder
import com.atlassian.jira.jql.query.QueryCreationContext
import com.atlassian.jira.jql.validator.NumberOfArgumentsValidator
import com.atlassian.jira.jql.parser.JqlQueryParser
import com.atlassian.jira.user.ApplicationUser
import com.atlassian.jira.util.MessageSet
import com.atlassian.query.clause.TerminalClause
import com.atlassian.query.operand.FunctionOperand
import groovy.util.logging.Log4j
import org.apache.lucene.search.Query

import java.text.MessageFormat

@Log4j
class JqlAliasFunction extends AbstractScriptedJqlFunction implements JqlQueryFunction {

    public static final String TEMPLATE_QUERY =
            ""

    @Override
    String getDescription() {
        "JQL Alias"
    }

    @Override
    MessageSet validate(ApplicationUser user, FunctionOperand operand, TerminalClause terminalClause) {
        def messageSet = new NumberOfArgumentsValidator(1, 1, getI18n()).validate(operand)

        if (messageSet.hasAnyErrors()) {
            return messageSet
        }

        def query = mergeQuery(operand)
        def searchService = ComponentAccessor.getComponent(SearchService)
        messageSet = searchService.validateQuery(user, query)
    }

    @Override
    List<Map> getArguments() {
        [
                [
                        description: "Search query",
                        optional: false,
                ]
        ]
    }

    @Override
    String getFunctionName() {
        "_jqlAlias"
    }

    private com.atlassian.query.Query mergeQuery(FunctionOperand operand) {
        def queryStr = MessageFormat.format(TEMPLATE_QUERY, operand.args.first())
        def queryParser = ComponentAccessor.getComponent(JqlQueryParser)
        queryParser.parseQuery(queryStr)
    }

    @Override
    Query getQuery(QueryCreationContext queryCreationContext, FunctionOperand operand, TerminalClause terminalClause) {
        String jql = operand.args.first()
        def queryParser = ComponentAccessor.getComponent(JqlQueryParser)
        def query = queryParser.parseQuery(jql)

//        def query = mergeQuery(operand)
        def luceneQueryBuilder = ComponentAccessor.getComponent(LuceneQueryBuilder)
        def luceneQuery = luceneQueryBuilder.createLuceneQuery(queryCreationContext, query.whereClause)
    }

}

