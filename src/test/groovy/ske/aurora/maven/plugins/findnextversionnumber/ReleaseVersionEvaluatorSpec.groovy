package ske.aurora.maven.plugins.versionnumber

import spock.lang.Specification
import spock.lang.Unroll

class ReleaseVersionEvaluatorSpec extends Specification {
  def "Suggested version number expands current version number by one segment"() {
    given:
      def existingVersions = ["1.2.1", "1.2.2", "1.3.1", "1.3.2", "1.3.3"]
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator("1-SNAPSHOT").
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == "1.3.4"
  }

  def "Suggested version number is X.0.0 when there are none existing version numbers to take into consideration"() {
    given:
      def existingVersions = ["1.2.1", "1.2.2", "1.3.1"]
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator("2.0.0-SNAPSHOT").
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == "2.0.0"
  }

  def "Suggested version number is X.X.0 when there are none existing version numbers to take into consideration"() {
    given:
      def existingVersions = ["1.2.1", "1.2.2", "1.3.1"]
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator("2.3-SNAPSHOT").
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == "2.3.0"
  }

  @Unroll
  def "Suggested version number is #expectedSuggestedReleaseVersion when existing versions are #existingVersions and current version is #currentVersion"() {
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator(currentVersion).
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == expectedSuggestedReleaseVersion

    where:
      expectedSuggestedReleaseVersion | currentVersion   | existingVersions
      "1.0.1"                         | "1.0-SNAPSHOT"   | ["1.0.0"]
      "1.0.1"                         | "1.0.0-SNAPSHOT" | ["1.0.0"]
      "1.0.2"                         | "1.0.0-SNAPSHOT" | ["1.0.0", "1.0.1", "2.0.1", "1.2.1"]
      "1.0.11"                        | "1.0.0-SNAPSHOT" | [(0..10).collect { "1.0.$it" }, "1.1.0"].flatten()
      "1.0.11"                        | "1.0.9-SNAPSHOT" | [(0..10).collect { "1.0.$it" }, "1.1.0"].flatten()
      "1.1.2"                         | "1.1-SNAPSHOT"   | ["1.0.0", "1.1.1", "1.1.0"]
  }

  def "version numbers not matching current version is not taken into consideration"() {
    given:
      def existingVersions = ["1.1.0", "2.1.0", "2.1.1", "2.2.0", "3.1.0"]
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator("2-SNAPSHOT").
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == "2.2.1"
  }

  def "SNAPSHOTS are excluded from evaluation"() {
    given:
      def existingVersions = ["1.2-SNAPSHOT", "1-SNAPSHOT", "1.3-SNAPSHOT", "2-SNAPSHOT"]
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator("1-SNAPSHOT").
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == "1.0.0"
  }

  def "The list of existing version numbers can be unordered"() {
    given:
      def existingVersions = ["1.2.0", "1.3.0", "1.2.1", "1.5.6", "1.6.2", "1.4.5"]
    when:
      def suggestedReleaseVersion = new ReleaseVersionEvaluator("1-SNAPSHOT").
          suggestNextReleaseVersionFrom(existingVersions);
    then:
      suggestedReleaseVersion.toString() == "1.6.3"
  }
}
