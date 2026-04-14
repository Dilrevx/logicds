{
    "vul_id": "M1", \\ your vulnerability id
    "project_name": "myBug", \\project name
    "language": "java",
    "vul_code_file_rel_path": "src/BuggyBuffer.java", \\path to buggy file relative to project
    "vul_code_func_start_line": 6, \\starting line of the buggy function
    "vul_code_func_end_line": 10, \\ inclusive
    "vul_code_block_start_line": 7, \\line of where your fix starts
    "vul_code_block_end_line": 9, \\ line where your fix ends, make sure all these lines are within the function
    "vul_code_func_fixed_start_line": 6, \\starting line of the function in the fixed version
    "vul_code_func_fixed_end_line": 12, \\inclusive
    "vul_code_block_fixed_start_line": 7, \\start of your fix
    "vul_code_block_fixed_end_line": 11, \\ end of your fix inclusive
    "vul_code_initial_block_start": 7, \\starting line of your fix
    "vul_code_initial_block_end": 7, \\same as previous field
    "vul_code_context_lines": ["3-7"], \\extra context lines, just give class member declarations of the main buggy class
    "context_lines": [{
        "src/BuggyBuffer.java": ["3-7"]
    }],
    "vul_description":  "Buffer Overflow",
    "vul_code_file":  "BuggyBuffer.java",
    "vul_code_lines":  ["7"], \\starting line of the buggy function
    "vul_code_line": 7 \\starting line of the buggy function
}
