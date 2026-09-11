#!/usr/bin/env python3
"""
Armer-Mann-Resolver: findet grossgeschriebene Bezeichner, die weder importiert
noch im selben Paket/der selben Datei deklariert sind. Faengt genau die Fehler,
die ohne Compiler sonst erst beim ersten Build auffallen.
"""
import os, re, sys, collections

ROOT = sys.argv[1] if len(sys.argv) > 1 else os.path.join(
    os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "app", "src", "main", "java")

STDLIB = set("""
String Int Boolean Float Double Long Short Byte Char Unit Any Nothing Number
List MutableList Set MutableSet Map MutableMap Pair Triple Array IntArray
FloatArray DoubleArray BooleanArray CharArray LongArray ByteArray ShortArray
Comparable Iterable Iterator Sequence Collection Throwable Exception Error
RuntimeException IllegalArgumentException IllegalStateException
System Math Companion Regex StringBuilder Result Lazy Function0 Function1
Deprecated DeprecationLevel Suppress JvmStatic JvmField JvmName Volatile
OptIn Target Retention RequiresOptIn Experimental Objects Double Boolean
T R K V E A B C D
""".split())

def strip(src):
    src = re.sub(r'"""(?:.|\n)*?"""', '""', src)
    src = re.sub(r'"(?:\\.|[^"\\\n])*"', '""', src)
    src = re.sub(r"'(?:\\.|[^'\\\n])'", "''", src)
    src = re.sub(r"/\*(?:.|\n)*?\*/", "", src)
    src = re.sub(r"//[^\n]*", "", src)
    return src

files = []
for dirpath, _, names in os.walk(ROOT):
    for n in names:
        if n.endswith(".kt"):
            files.append(os.path.join(dirpath, n))
files.sort()

# 1. Alle Top-Level-Deklarationen je Paket sammeln
paket_decls = collections.defaultdict(set)
datei_info = {}
MODS = (r'(?:public\s+|internal\s+|private\s+|protected\s+|abstract\s+|open\s+|sealed\s+'
        r'|data\s+|value\s+|enum\s+|annotation\s+|inline\s+|const\s+|lateinit\s+'
        r'|external\s+|expect\s+|actual\s+|override\s+|companion\s+|operator\s+|infix\s+|suspend\s+)*')
DECL_TYP = re.compile(r'^\s*(?:@\w+(?:\([^)]*\))?\s*)*' + MODS +
                      r'(?:class|interface|object|typealias)\s+([A-Za-z_]\w*)', re.M)
# fun kann einen Empfaengertyp tragen: fun Foo.Bar.name(...)
DECL_FUN = re.compile(r'^\s*(?:@\w+(?:\([^)]*\))?\s*)*' + MODS +
                      r'fun\s+(?:<[^>]*>\s*)?(?:[\w.]+\.)?([A-Za-z_]\w*)\s*\(', re.M)
DECL_PROP = re.compile(r'^\s*(?:@\w+(?:\([^)]*\))?\s*)*' + MODS +
                       r'(?:val|var)\s+(?:<[^>]*>\s*)?(?:[\w.]+\.)?([A-Za-z_]\w*)', re.M)
ENUM_BLOCK = re.compile(r'enum\s+class\s+\w+[^{]*\{(.*?)(?:;|\})', re.S)

for f in files:
    src = open(f, encoding="utf-8").read()
    clean = strip(src)
    m = re.search(r'^package\s+([\w.]+)', clean, re.M)
    pkg = m.group(1) if m else ""
    imports = {}
    for im in re.finditer(r'^import\s+([\w.]+)(?:\s+as\s+(\w+))?', clean, re.M):
        pfad, alias = im.group(1), im.group(2)
        imports[alias or pfad.split(".")[-1]] = pfad
    lokal = set()
    for rx in (DECL_TYP, DECL_FUN, DECL_PROP):
        for d in rx.finditer(clean):
            lokal.add(d.group(1))
    # Enum-Eintraege
    for block in ENUM_BLOCK.finditer(clean):
        for stueck in block.group(1).split(","):
            treffer = re.match(r'\s*([A-Z][A-Za-z0-9_]*)', stueck)
            if treffer:
                lokal.add(treffer.group(1))
    # Sealed-Interface-Mitglieder: data object X / data class X
    for d in re.finditer(r'\bdata\s+(?:object|class)\s+([A-Z]\w*)', clean):
        lokal.add(d.group(1))
    paket_decls[pkg] |= {n for n in lokal if n and n[0].isupper()}
    datei_info[f] = (pkg, imports, lokal, clean)

# 2. Referenzen pruefen
REF = re.compile(r'(?<![\w.@])([A-Z][A-Za-z0-9_]*)')
probleme = collections.defaultdict(list)
for f, (pkg, imports, lokal, clean) in datei_info.items():
    body = re.sub(r'^\s*(?:package|import)[^\n]*\n', '', clean, flags=re.M)
    for zeilennr, zeile in enumerate(body.split("\n"), 1):
        for m in REF.finditer(zeile):
            name = m.group(1)
            if name in STDLIB or name in imports or name in lokal:
                continue
            if name in paket_decls.get(pkg, set()):
                continue
            if len(name) <= 2:
                continue
            probleme[f].append(name)

# 3. Projektinterne Importe gegen die tatsaechlichen Deklarationen pruefen
fehlimporte = collections.defaultdict(list)
alle_namen = collections.defaultdict(set)
for f, (pkg, imports, lokal, clean) in datei_info.items():
    alle_namen[pkg] |= lokal
for f, (pkg, imports, lokal, clean) in datei_info.items():
    for name, pfad in imports.items():
        if not pfad.startswith("de.kopfgeld.app"):
            continue
        zielpaket, _, symbol = pfad.rpartition(".")
        if symbol not in alle_namen.get(zielpaket, set()):
            # Koennte ein Mitglied eines Typs sein (z. B. Enum-Eintrag)
            elternpaket, _, typ = zielpaket.rpartition(".")
            if typ not in alle_namen.get(elternpaket, set()):
                fehlimporte[f].append(pfad)

for f in sorted(fehlimporte):
    print("IMPORT ZEIGT INS LEERE:", os.path.relpath(f, ROOT))
    for pfad in fehlimporte[f]:
        print("   ", pfad)

gesamt = 0
for f in sorted(probleme):
    zaehler = collections.Counter(probleme[f])
    print(os.path.relpath(f, ROOT))
    for name, n in zaehler.most_common():
        print(f"    {name}  ({n}x)")
        gesamt += 1
print("---")
print(f"{len(files)} Dateien, {gesamt} ungeklaerte Bezeichner, "
      f"{sum(len(v) for v in fehlimporte.values())} tote Importe")
