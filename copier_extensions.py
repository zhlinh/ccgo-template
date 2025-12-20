import re
import subprocess
import unicodedata
from datetime import date

from jinja2.ext import Extension


def git_user_name(default: str="") -> str:
    return (
            subprocess.getoutput("git config user.name").strip() 
            or subprocess.getoutput("git config --global user.name").strip() 
            or default
    )


def git_user_email(default: str="") -> str:
    return (
            subprocess.getoutput("git config user.email").strip() 
            or subprocess.getoutput("git config --global user.email").strip() 
            or default
    )

def git_to_https(url: str) -> str:
    return (
            url.replace('.git', '')
               .replace(':', '/')
               .replace('git@', 'https://')
    )


def slugify(value, separator="-"):
    value = unicodedata.normalize("NFKD", str(value)).encode("ascii", "ignore").decode("ascii")
    value = re.sub(r"[^\w\s-]", "", value.lower())
    return re.sub(r"[-_\s]+", separator, value).strip("-_")


def macro_safe(value):
    """Convert a string to a valid C/C++ macro name by replacing hyphens with underscores."""
    return str(value).replace("-", "_")


def pascal_case(value):
    """Convert a hyphen-separated string to PascalCase for Java class names.

    Example: 'ccgo-test-build' -> 'CcgoTestBuild'
    """
    parts = str(value).split("-")
    return "".join(part.capitalize() for part in parts)


class GitExtension(Extension):
    def __init__(self, environment):
        super().__init__(environment)
        environment.globals["git_user_name"] = git_user_name
        environment.globals["git_user_email"] = git_user_email
        environment.filters["git_to_https"] = git_to_https


class SlugifyExtension(Extension):
    def __init__(self, environment):
        super().__init__(environment)
        environment.filters["slugify"] = slugify


class MacroSafeExtension(Extension):
    def __init__(self, environment):
        super().__init__(environment)
        environment.filters["macro_safe"] = macro_safe


class PascalCaseExtension(Extension):
    def __init__(self, environment):
        super().__init__(environment)
        environment.filters["pascal_case"] = pascal_case


class CurrentYearExtension(Extension):
    def __init__(self, environment):
        super().__init__(environment)
        environment.globals["current_year"] = lambda: date.today().strftime('%Y')
        environment.globals["current_day"] = lambda: date.today().strftime('%Y-%m-%d')
        environment.filters["current_year"] = lambda x: date.today().strftime('%Y')