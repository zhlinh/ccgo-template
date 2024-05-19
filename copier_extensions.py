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


class CurrentYearExtension(Extension):
    def __init__(self, environment):
        super().__init__(environment)
        environment.globals["current_year"] = date.today().strftime('%Y')
        environment.globals["current_day"] = date.today().strftime('%Y-%m-%d')