package org.apache.poi.hssf.usermodel;

import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.model.Workbook;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.NameRecord;
import org.apache.poi.hssf.record.aggregates.FormulaRecordAggregate;
import org.apache.poi.hssf.record.formula.NamePtg;
import org.apache.poi.hssf.record.formula.NameXPtg;
import org.apache.poi.hssf.record.formula.Ptg;
import org.apache.poi.ss.formula.EvaluationName;
import org.apache.poi.ss.formula.EvaluationWorkbook;
import org.apache.poi.ss.formula.FormulaParsingWorkbook;
import org.apache.poi.ss.formula.FormulaRenderingWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;

/**
 * Internal POI use only
 * 
 * @author Josh Micich
 */
public final class HSSFEvaluationWorkbook implements FormulaRenderingWorkbook, EvaluationWorkbook, FormulaParsingWorkbook {

	private final HSSFWorkbook _uBook;
	private final Workbook _iBook;
	
	public static HSSFEvaluationWorkbook create(HSSFWorkbook book) {
		if (book == null) {
			return null;
		}
		return new HSSFEvaluationWorkbook(book);
	}

	private HSSFEvaluationWorkbook(HSSFWorkbook book) {
		_uBook = book;
		_iBook = book.getWorkbook();
	}

	public int getExternalSheetIndex(String sheetName) {
		int sheetIndex = _uBook.getSheetIndex(sheetName);
		return _iBook.checkExternSheet(sheetIndex);
	}
	public int getExternalSheetIndex(String workbookName, String sheetName) {
		return _iBook.getExternalSheetIndex(workbookName, sheetName);
	}

	public EvaluationName getName(int index) {
		return new Name(_iBook.getNameRecord(index), index);
	}

	public EvaluationName getName(String name) {
		for(int i=0; i < _iBook.getNumNames(); i++) {
			NameRecord nr = _iBook.getNameRecord(i);
			if (name.equalsIgnoreCase(nr.getNameText())) {
				return new Name(nr, i);
			}
		}
		return null;
	}

	public int getSheetIndex(Sheet sheet) {
		return _uBook.getSheetIndex(sheet);
	}
	public int getSheetIndex(String sheetName) {
		return _uBook.getSheetIndex(sheetName);
	}

	public String getSheetName(int sheetIndex) {
		return _uBook.getSheetName(sheetIndex);
	}

	public int getNameIndex(String name) {
		return _uBook.getNameIndex(name);
	}

	public NameXPtg getNameXPtg(String name) {
		return _iBook.getNameXPtg(name);
	}

	public Sheet getSheet(int sheetIndex) {
		return _uBook.getSheetAt(sheetIndex);
	}
	public int convertFromExternSheetIndex(int externSheetIndex) {
		return _iBook.getSheetIndexFromExternSheetIndex(externSheetIndex);
	}

	public ExternalSheet getExternalSheet(int externSheetIndex) {
		return _iBook.getExternalSheet(externSheetIndex);
	}
	
	public HSSFWorkbook getWorkbook() {
		return _uBook;
	}

	public String resolveNameXText(NameXPtg n) {
		return _iBook.resolveNameXText(n.getSheetRefIndex(), n.getNameIndex());
	}

	public String getSheetNameByExternSheet(int externSheetIndex) {
		return _iBook.findSheetNameFromExternSheet(externSheetIndex);
	}
	public String getNameText(NamePtg namePtg) {
		return _iBook.getNameRecord(namePtg.getIndex()).getNameText();
	}
	public EvaluationName getName(NamePtg namePtg) {
		int ix = namePtg.getIndex();
		return new Name(_iBook.getNameRecord(ix), ix);
	}
	public Ptg[] getFormulaTokens(Cell cell) {
		if (false) {
			// re-parsing the formula text also works, but is a waste of time
			// It is useful from time to time to run all unit tests with this code
			// to make sure that all formulas POI can evaluate can also be parsed.
			return HSSFFormulaParser.parse(cell.getCellFormula(), _uBook);
		}
		HSSFCell hCell = (HSSFCell) cell;
		FormulaRecord fr = ((FormulaRecordAggregate) hCell.getCellValueRecord()).getFormulaRecord();
		return fr.getParsedExpression();
	}

	private static final class Name implements EvaluationName {

		private final NameRecord _nameRecord;
		private final int _index;

		public Name(NameRecord nameRecord, int index) {
			_nameRecord = nameRecord;
			_index = index;
		}

		public Ptg[] getNameDefinition() {
			return _nameRecord.getNameDefinition();
		}

		public String getNameText() {
			return _nameRecord.getNameText();
		}

		public boolean hasFormula() {
			return _nameRecord.hasFormula();
		}

		public boolean isFunctionName() {
			return _nameRecord.isFunctionName();
		}

		public boolean isRange() {
			return _nameRecord.hasFormula(); // TODO - is this right?
		}
		public NamePtg createPtg() {
			return new NamePtg(_index);
		}
	}
}
