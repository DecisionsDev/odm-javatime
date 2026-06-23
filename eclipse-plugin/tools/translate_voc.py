#!/usr/bin/env python3
"""
Generate translated VOC files for both compatible and strict modes from a unified translation YAML file.

Usage:
    python translate_voc.py <yaml_file>
    
Arguments:
    yaml_file: Path to the translation YAML file (e.g., translations_fr_FR.yaml)
    
Example:
    python translate_voc.py translations_fr_FR.yaml
    
This will generate both:
    - compatible/javatime-compatible_{locale}.voc (with prefix from YAML)
    - strict/javatime-strict_{locale}.voc (without prefix)
"""

import sys
import yaml
import os


def load_translations(yaml_file):
    """Load translations from YAML file."""
    with open(yaml_file, 'r', encoding='utf-8') as f:
        data = yaml.safe_load(f)
    return data['locale'], data.get('time_prefix', 'time.'), data['translations']


def translate_voc_file(template_file, output_file, translations, mode, prefix):
    """
    Translate a VOC template file using the translations dictionary.
    
    Args:
        template_file: Path to the English template VOC file
        output_file: Path to the output translated VOC file
        translations: Dictionary of English -> translated text
        mode: 'compatible' or 'strict'
        prefix: Prefix to add in compatible mode (e.g., 'temps.')
    """
    with open(template_file, 'r', encoding='utf-8') as f:
        lines = f.readlines()
    
    translated_lines = []
    translation_count = 0
    
    for line in lines:
        # Check if line contains a translatable value (after '=')
        if '=' in line and not line.strip().startswith('#'):
            parts = line.split('=', 1)
            if len(parts) == 2:
                key_part = parts[0]
                value_part = parts[1].strip()
                
                # Track if we removed a prefix (for compatible mode)
                prefix_removed = False
                
                # For compatible mode, strip 'time.' prefix from English to get base value
                if mode == 'compatible' and value_part.startswith('time.'):
                    base_english = value_part[5:]  # Remove 'time.' prefix
                    prefix_removed = True
                else:
                    base_english = value_part
                
                # Look up translation
                if base_english in translations:
                    translated_value = translations[base_english]
                    
                    # Add prefix for compatible mode if we removed it from English
                    if mode == 'compatible' and prefix_removed:
                        translated_value = prefix + translated_value
                    
                    translation_count += 1
                    translated_lines.append(f"{key_part}= {translated_value}\n")
                else:
                    # Keep original if no translation found
                    translated_lines.append(line)
            else:
                translated_lines.append(line)
        else:
            # Keep comments and other lines as-is
            translated_lines.append(line)
    
    # Write translated file
    with open(output_file, 'w', encoding='utf-8') as f:
        f.writelines(translated_lines)
    
    return translation_count


def main():
    if len(sys.argv) != 2:
        print("Usage: python translate_voc.py <yaml_file>")
        print("Example: python translate_voc.py translations_fr_FR.yaml")
        sys.exit(1)
    
    yaml_file = sys.argv[1]
    
    if not os.path.exists(yaml_file):
        print(f"Error: YAML file not found: {yaml_file}")
        sys.exit(1)
    
    # Load translations
    locale, prefix, translations = load_translations(yaml_file)
    print(f"\nLoaded {len(translations)} translations for locale: {locale}")
    print(f"Compatible mode prefix: {prefix}")
    print("=" * 70)
    
    # Generate both modes
    modes = ['compatible', 'strict']
    total_files = 0
    total_translations = 0
    
    for mode in modes:
        print(f"\nGenerating {mode.upper()} mode:")
        print("-" * 70)
        
        # Determine template and output paths
        template_file = f"{mode}/javatime-{mode}_en_US.voc"
        output_file = f"{mode}/javatime-{mode}_{locale}.voc"
        
        if not os.path.exists(template_file):
            print(f"  [ERROR] Template file not found: {template_file}")
            continue
        
        # Translate the VOC file
        count = translate_voc_file(template_file, output_file, translations, mode, prefix)
        print(f"  [OK] Generated: {output_file}")
        print(f"       Translations applied: {count}")
        
        total_files += 1
        total_translations += count
    
    print("\n" + "=" * 70)
    print("Translation complete!")
    print(f"  Locale: {locale}")
    print(f"  Files generated: {total_files}")
    print(f"  Total translations: {total_translations}")


if __name__ == '__main__':
    main()


