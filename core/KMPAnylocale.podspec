Pod::Spec.new do |spec|
    spec.name                     = 'KMPAnylocale'
    spec.version                  = '1.0.0-alpha03'
    spec.homepage                 = 'https://github.com/anylocale/anylocale-android'
    spec.source                   = { :http=> ''}
    spec.authors                  = ''
    spec.license                  = 'Apache License 2.0'
    spec.summary                  = 'Kotlin Multiplatform localization wrapper for Anylocale'
    spec.vendored_frameworks      = 'build/cocoapods/framework/KMPAnylocale.framework'
    spec.libraries                = 'c++'
                
                
                
    if !Dir.exist?('build/cocoapods/framework/KMPAnylocale.framework') || Dir.empty?('build/cocoapods/framework/KMPAnylocale.framework')
        raise "

        Kotlin framework 'KMPAnylocale' doesn't exist yet, so a proper Xcode project can't be generated.
        'pod install' should be executed after running ':generateDummyFramework' Gradle task:

            ./gradlew :core:generateDummyFramework

        Alternatively, proper pod installation is performed during Gradle sync in the IDE (if Podfile location is set)"
    end
                
    spec.xcconfig = {
        'ENABLE_USER_SCRIPT_SANDBOXING' => 'NO',
    }
                
    spec.pod_target_xcconfig = {
        'KOTLIN_PROJECT_PATH' => ':core',
        'PRODUCT_MODULE_NAME' => 'KMPAnylocale',
    }
                
    spec.script_phases = [
        {
            :name => 'Build KMPAnylocale',
            :execution_position => :before_compile,
            :shell_path => '/bin/sh',
            :script => <<-SCRIPT
                if [ "YES" = "$OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED" ]; then
                  echo "Skipping Gradle build task invocation due to OVERRIDE_KOTLIN_BUILD_IDE_SUPPORTED environment variable set to \"YES\""
                  exit 0
                fi
                set -ev
                REPO_ROOT="$PODS_TARGET_SRCROOT"
                "$REPO_ROOT/../gradlew" -p "$REPO_ROOT" $KOTLIN_PROJECT_PATH:syncFramework \
                    -Pkotlin.native.cocoapods.platform=$PLATFORM_NAME \
                    -Pkotlin.native.cocoapods.archs="$ARCHS" \
                    -Pkotlin.native.cocoapods.configuration="$CONFIGURATION"
            SCRIPT
        }
    ]
                
end