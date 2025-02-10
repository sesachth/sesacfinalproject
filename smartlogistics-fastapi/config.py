from pydantic_settings import BaseSettings, SettingsConfigDict
from pydantic import MySQLDsn

class Settings(BaseSettings):
    model_config = SettingsConfigDict(env_file=".env", env_file_encoding="utf-8")

    PROJECT_NAME: str = "SmartLogistics-FastAPI-v1"

    DB_HOST: str
    DB_PORT: int
    DB_USER: str
    DB_PASSWORD: str
    DB_NAME: str

    @property
    def DATABASE_URL(self) -> MySQLDsn:
        return MySQLDsn.build(
            scheme="mysql+pymysql",
            host=self.DB_HOST,
            port=self.DB_PORT,
            path=self.DB_NAME,
            username=self.DB_USER,
            password=self.DB_PASSWORD,
        )

settings = Settings()