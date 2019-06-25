package org.nuisto

import groovy.util.logging.Slf4j
import org.nuisto.model.OptionsModel
import org.nuisto.results.JsonResultsHandler

@Slf4j(category = 'org.nuisto.msa')
class MuleLint {
  static void main(String [] args) {
    int result = new MuleLint().run(args)

    System.exit(result)
  }

  int run(String [] args) {
    //TODO Need to look into updated the CliBuilder used https://dzone.com/articles/groovy-25-clibuilder-renewal

    def cli = new CliBuilder(usage: 'mule-lint.groovy [-h] -s <sourceDirectory> -r <rules> -d <dictionaryFile>')
    // Create the list of options.
    cli.with {
      d(longOpt: 'dictionary', args: 1, argName: 'file',     'A dictionary file of known words')
      h(longOpt: 'help',                                     'Show usage information')
      r(longOpt: 'rules',      args: 1, argName: 'path',     'Required. The path to a set of rules.')
      s(longOpt: 'sources',    args: 1, argName: 'sources',  'The directory name of where the source files are located, default: src/main')
      o(longOpt: 'output',     args: 1, argName: 'path',     'The file name to write json results to.')
      _(longOpt: 'exclude',    args: 1, argName: 'pattern',  'Patterns to exclude (i.e. **/*.doc) uses FileNameFinder')
      _(longOpt: 'fail-build',                                'Whether to fail the build, just the existing of this key causes a failure, no need to set it to anything.')
    }

    def options = cli.parse(args)
    if (!options) {
      return ErrorCodes.OptionsNotProvided
    }

    // Show usage text when -h or --help option is used.
    if (options.h) {
      cli.usage()
      return ErrorCodes.Success
    }

    def optionsModel = new OptionsModel()

    if (!options.r) {
      log.error 'Rules must be provided'
      return ErrorCodes.RulesNotProvided
    }
    else {
      optionsModel.rules = options.r
    }

    if (options.s) {
      optionsModel.sourceDirectory = options.s
    }
    else {
      optionsModel.sourceDirectory = 'src/main'
    }

    if (options.d) {
      optionsModel.dictionary = options.d
    }

    if (options.o) {
      optionsModel.resultsFile = options.o
    }

    if (options.excludes) {
      optionsModel.excludePatterns = options.excludes
    }

    if (options.hasOption('fail-build')) {
      optionsModel.failBuild = true
    }

    try {
      runWithModel(optionsModel)

      return ErrorCodes.Success
    }
    catch (Exception ex) {
      return ErrorCodes.GenericFailure
    }
  }

  void invoke(boolean failBuild, String dictionary, String rules, String sourceDirectory, String outputFile, String [] excludePatterns, Map<String, String> namespaces) {
    OptionsModel optionsModel = new OptionsModel(
            failBuild: failBuild,
            dictionary: dictionary,
            rules: rules,
            resultsFile: outputFile,
            sourceDirectory: sourceDirectory,
            excludePatterns: excludePatterns,
            namespaces: namespaces
    )

    runWithModel(optionsModel)
  }

  void runWithModel(OptionsModel model) {
    new Runner(new JsonResultsHandler()).runWithModel(model)
  }
}
