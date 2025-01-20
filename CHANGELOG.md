# Changelog

### Newer versions
See [Release page at Jenkins Plugin Index](https://plugins.jenkins.io/antexec/releases/)


### 1.11
Release date: _2015-08-18_

- Passing parameters and properties through the generated property file instead of command-line which caused problems (especially on Windows)
- Changed core dependency from `1.480` to `1.596`
- Updated dependency on "token-macro" plugin (1.10)
- Dropped compatibility with Java 1.5

### 1.10 
Release date: _2013-10-31_

- Load environment variables with `<property environment="env"/>` by default, so they are accessible via ${env.VARIABLE}
- Fixed missing job options in command line properties

### 1.9
Release date: _2013-07-15_

- Build files are now automatically deleted by default so you need to update job configuration if you want to keep them
- Fixing upgrade issue with ant-contrib in version 1.8

### 1.8
Release date: _2013-06-28_

- Remove archive copying of buildfile to job directory
- Cleanup dependencies in pom.xml

### 1.7 
Release date: _2013-06-24_

- Added option to keep build files for each build step, it's deleted by default now
- Added dependency on "ant" plugin (1.2)
- Added dependency on "token-macro" plugin (1.7)
- Option for disabling  ant-contrib has been moved to build step configuration
- Changed dependency to core 1.480

### 1.6 
Release date: _2012-08-28_

- Fixing typo in extendedScriptSource

### 1.5
Release date: _2012-06-21_

- Fixing typo

### 1.4
Release date: _2012-06-20_

- Fixing build file when extended script source is null

### 1.3
Release date: _2012-06-06_

- First public release