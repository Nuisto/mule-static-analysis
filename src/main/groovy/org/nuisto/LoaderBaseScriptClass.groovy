package org.nuisto

import groovy.util.logging.Slf4j

/**
 * Needs to follow more closely to the builder pattern
 *
 * When a method is first called, expect that is a new
 * line in the rules and a new expectation.
 *
 * Second thought - maybe it just creates a builder and
 * hands off each method call to a "flow builder" or
 * "element builder". The builder would need to be
 * based on an interface so each method invocation
 * we can get the constructed object and add it to
 * a collection
 */
@Slf4j(category = 'org.nuisto.msa')
abstract class LoaderBaseScriptClass extends Script {
  String are = 'are'
  String cased = 'cased'

  ExpectationBuilder builder = new NullExpectationBuilder()

  /**
   * TODO See the comment in RulesLoader, this should be removed from here
   */
  void version(String version) {
  }

  FlowExpectationBuilder flows(String ignore) {
    //Ignored parameter is the value of the 'are' member

    builder.addBuiltExpectationTo(this.binding.expectations)
    builder = new FlowExpectationBuilder()
    return builder
  }

  ElementExpectationBuilder element(String name) {
    builder.addBuiltExpectationTo(this.binding.expectations)
    builder = new ElementExpectationBuilder(name)
    return builder
  }
}
