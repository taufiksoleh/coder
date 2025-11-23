# CMake configuration for terminal native library
cmake_minimum_required(VERSION 3.22.1)

project("terminal")

add_library(terminal SHARED
    terminal.cpp
)

find_library(log-lib log)

target_link_libraries(terminal
    ${log-lib}
)
