#!/usr/bin/env python3
"""
Generate a generic translation template (locale: xx_XX) with all translatable strings.
Only excludes lines with .precedence
"""

import yaml

def extract_strings(voc_file):
    """Extract all strings except .precedence, #sortIndex, #predicate.group, and #predicate lines."""
    strings = set()
    
    with open(voc_file, 'r', encoding='utf-8') as f:
        for line in f:
            # Skip comments, empty lines, and technical property lines
            if line.strip().startswith('#') or not line.strip():
                continue
            if '.precedence' in line or '#sortIndex' in line or '#predicate.group' in line or '#predicate.' in line:
                continue
            
            # Extract value after '='
            if '=' in line:
                parts = line.split('=', 1)
                if len(parts) == 2:
                    value = parts[1].strip()
                    # Skip variables like ${UUID}
                    if value and not value.startswith('${'):
                        strings.add(value)
    
    return sorted(strings)

def main():
    # Extract all strings from English VOC (except .precedence)
    all_strings = extract_strings('strict/javatime-strict_en_US.voc')
    
    # Create translations dict with numbered keys
    translations = {}
    reference_lines = []
    
    for i, string in enumerate(all_strings, 1):
        key = f'@TR{i:03d}'
        translations[string] = key
        reference_lines.append(f'{key}: {string}')
    
    # Create generic YAML structure
    yaml_data = {
        'locale': 'xx_XX',
        'time_prefix': 'time.',
        'translations': translations
    }
    
    # Write generic template
    output_file = 'translations_xx_XX.yaml'
    with open(output_file, 'w', encoding='utf-8') as f:
        f.write('# Generic translation template\n')
        f.write('# 1. Replace xx_XX with your locale (e.g., fr_FR, es_ES, de_DE)\n')
        f.write('# 2. Replace time_prefix with localized prefix:\n')
        f.write('#    - French: temps.\n')
        f.write('#    - Spanish: tiempo.\n')
        f.write('#    - German: zeit.\n')
        f.write('#    - English: time.\n')
        f.write('# 3. Replace @TRxxx keys with translations in your language\n')
        f.write('# 4. Keep placeholders like {0}, {1}, {this} unchanged\n\n')
        f.write(f'locale: {yaml_data["locale"]}\n')
        f.write(f'time_prefix: {yaml_data["time_prefix"]}\n')
        f.write('translations:\n')
        for key, value in yaml_data['translations'].items():
            # Escape single quotes in key and use single quotes to avoid multi-line format
            escaped_key = key.replace("'", "''")
            f.write(f"  '{escaped_key}': '{value}'\n")
    
    # Write reference file
    reference_file = 'translation_keys_generic.txt'
    with open(reference_file, 'w', encoding='utf-8') as f:
        f.write('# Generic Translation Reference\n')
        f.write('# All strings from VOC file (excluding technical properties)\n\n')
        for line in reference_lines:
            f.write(line + '\n')
    
    print(f"Generated generic translation template: {output_file}")
    print(f"  Locale: xx_XX (replace with your locale)")
    print(f"  Total strings: {len(translations)}")
    print(f"\nGenerated reference file: {reference_file}")
    print(f"  Translation keys: @TR001 to @TR{len(translations):03d}")
    print(f"\nExcluded: .precedence, #sortIndex, #predicate.group, #predicate.*")

if __name__ == '__main__':
    main()


