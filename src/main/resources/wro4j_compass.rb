ENV['GEM_HOME'] = $compass_dir + '/.gems'
puts "Compass dir: #{$compass_dir}"

require 'rubygems'
require 'rubygems/dependency_installer'

gem_installer = Gem::DependencyInstaller.new

gem_installer.install("compass")
gem_installer.install("sass")

require 'compass'
require 'compass/commands'
require 'sass'
require 'sass/plugin'



module Compass

  class Compiler

    def compile_string(str, sass_file_name)
       start_time = end_time = nil
        css_content = logger.red do
          timed do
            engine_string(str, sass_file_name).render
          end
        end
        duration = options[:time] ? "(#{(css_content.__duration * 1000).round / 1000.0}s)" : ""
       css_content
    end

    def engine_string(str, sass_file_name)
      syntax = (sass_file_name =~ /\.(s[ac]ss)$/) && $1.to_sym || :sass
      opts = sass_options.merge(:filename => sass_file_name, :syntax => syntax, :force => true)
      Sass::Engine.new(str, opts)
    end
  end

end

def compile_compass(content, real_file_name)
  cmd = Compass::Commands::UpdateProject.new($compass_dir, {:sass_files => real_file_name})
  compiler = cmd.new_compiler_instance
  compiler.compile_string(content, real_file_name)
end