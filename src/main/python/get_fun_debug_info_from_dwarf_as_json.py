import elftools.elf.elffile
import json
import sys

# Modification of:
# https://github.com/eliben/pyelftools/blob/main/examples/dwarf_lineprogram_filenames.py

fun_map = {}

with (open(sys.argv[1], "rb") as fd):
    elf_file = elftools.elf.elffile.ELFFile(fd)
    assert elf_file.has_dwarf_info()
    dwarf_info = elf_file.get_dwarf_info()
    for cu in dwarf_info.iter_CUs():
        line_program = dwarf_info.line_program_for_CU(cu)
        if line_program is None:
            print('DWARF info is missing a line program for a CU!',
                  file=sys.stderr)
            continue

        def get_file_entry(file_index):
            if file_index is None: return (None, None)
            lp_header = line_program.header
            if lp_header.version < 5:
                file_index -= 1
            if file_index == -1:
                return (None, None)
            file_entry = lp_header["file_entry"][file_index]
            filename = file_entry["name"].decode()
            dir_index = file_entry["dir_index"]
            if dir_index == 0 and lp_header.version < 5:
                return (None, filename)
            if lp_header.version < 5:
                dir_index -= 1
            return (lp_header["include_directory"][dir_index].decode(), filename)

        for die in cu.iter_DIEs():
            if die.tag != 'DW_TAG_subprogram':
                continue

            def get_attr_or_null(attr):
                if attr in die.attributes:
                    value = die.attributes[attr].value
                    if isinstance(value, bytes): return value.decode()
                    else: return value
                return None

            linkage_name = get_attr_or_null("DW_AT_MIPS_linkage_name")
            if linkage_name is None: continue
            dir, filename = get_file_entry(get_attr_or_null("DW_AT_decl_file"))
            fun_map[linkage_name] = {
                "name" : get_attr_or_null("DW_AT_name"),
                "dir" : dir,
                "filename" : filename,
                "line" : get_attr_or_null("DW_AT_decl_line")
            }

print(json.dumps(fun_map))
